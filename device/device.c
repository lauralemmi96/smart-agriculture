#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "sys/etimer.h"
#include "dev/leds.h"
#include "os/dev/serial-line.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP
/*	Declare server IP	*/
#define SERVER_EP "coap://[fd00::1]:5683" 
#define REQ_INTERVAL 5

/*	Declare external resources to be activated	*/
extern coap_resource_t res_humidity;
extern coap_resource_t res_sprinkler;
extern coap_resource_t res_temp;
extern coap_resource_t res_light;
extern coap_resource_t res_unregister;

//UNREGISTER event generated by the res_unregister
extern process_event_t UNREGISTER;


static struct etimer e_timer;	//Timer
char *reg_service_url = "/registration";	//Resource URL for registration
static bool registration_status = false;
static int device_type = 0;	//0: sensor;	1: actuator;	2: both


/* Declare and auto-start this file's process */
PROCESS(device_process, "Sensor/Actuator Device");
AUTOSTART_PROCESSES(&device_process);


/*------------------------------------------------------------------------------------------*/

/*	Handler for the response from theserver	*/

void client_chunk_handler(coap_message_t *response) {
 
	if(response == NULL) { 
		printf("Request timed out\n"); 
		return;
	}
	const uint8_t *chunk;
	coap_get_payload(response, &chunk);
	printf("Received Response: %s\n", (char *)chunk);

	//If the response is "Accept" I registered the device
	if(strcmp( (char *)chunk, "Accept") == 0)
		registration_status = true;
	else
		registration_status = false;

}

/*------------------------------------------------------------------------------------------*/

PROCESS_THREAD(device_process, ev, data){

	static coap_endpoint_t server_ep;
  	static coap_message_t request[1];  /* the packet can be treated as pointer */

	PROCESS_BEGIN();

	/*	Get device type from command line and activate the related resources	*/
	while(1){
		printf("\nType the kind of device you want to deploy: \"sensor\", \"actuator\", \"both\"\n");
		PROCESS_WAIT_EVENT_UNTIL(ev == serial_line_event_message);
		//Sensor => I activate sensor resources
		if(strcmp(data, "sensor") == 0){
			//No led needed
			device_type = 0;

			/*	Resource activation	*/
			coap_activate_resource(&res_humidity, "humidity");
			coap_activate_resource(&res_temp, "temperature");
			coap_activate_resource(&res_unregister, "unregister");
			break;
		}
		//Actuator => I activate actuator resources
		else if(strcmp(data, "actuator") == 0){
			device_type = 1;

			/*	Resource activation	*/
			coap_activate_resource(&res_sprinkler, "sprinkler");
			coap_activate_resource(&res_light, "light");
			coap_activate_resource(&res_unregister, "unregister");
			//Activate RED led - sprinkler && light off 
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
			break;
		}
		//Both => I activate all resources
		else if(strcmp(data, "both") == 0){
			device_type = 2;

			/*	Resource activation	*/

			//sensors
			coap_activate_resource(&res_humidity, "humidity");
			coap_activate_resource(&res_temp, "temperature");

			//actuators
			coap_activate_resource(&res_sprinkler, "sprinkler");
			coap_activate_resource(&res_light, "light");
			//Activate RED led - sprinkler && light off
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));

			coap_activate_resource(&res_unregister, "unregister");
			break;
		}
	}

	
	printf("Type \"register\" for registering to the cloud application\n"); 

	while(1){

		//Possible events: timer, unregister event, serial line message
		PROCESS_WAIT_EVENT_UNTIL(ev == PROCESS_EVENT_TIMER || ev == UNREGISTER || ev == serial_line_event_message);

		//Only if registered I handle the timer event
		if(ev == PROCESS_EVENT_TIMER && registration_status){
			//Depend on device type I observe different resources
		  	if((device_type == 0) || (device_type == 2)){
				res_humidity.trigger();
				res_temp.trigger();
			}else if((device_type == 1) || (device_type == 2)){
				res_sprinkler.trigger();
				res_light.trigger();
			}
			
			etimer_reset(&e_timer);
		}
		//Only if !registered I accept command line values
		else if(ev == serial_line_event_message && !registration_status){
			//I register the device
			if(strcmp(data, "register") == 0){
				/*	Node Registration	*/

				// Populate the coap_endpoint_t data structure
				coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
				// Prepare the message
				coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
				coap_set_header_uri_path(request, reg_service_url);

				while(!registration_status){
					LOG_INFO("Sending registration request to the server\n");
					//Send the registration request to the server
					COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
				}

				printf("Registration status: %s\n", registration_status ? "true" : "false");		
				etimer_set(&e_timer, CLOCK_SECOND * REQ_INTERVAL);
				

			}
		}
		//If UNREGISTER, I unregister the device and stop the timer -> No more observation needed
		else if(ev == UNREGISTER){

			registration_status = false;
			etimer_stop(&e_timer);
			printf("Registration status set to: %s\n", registration_status ? "true" : "false");
			printf("\nType \"register\" for registering to the cloud application\n");
		}
	}
  	PROCESS_END();
}
