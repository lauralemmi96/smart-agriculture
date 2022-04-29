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
#define REQ_INTERVAL 20

/*	Declare external resources to be activated	*/
extern coap_resource_t res_humidity;
extern coap_resource_t res_sprinkler;
extern coap_resource_t res_temp;
extern coap_resource_t res_light;


static struct etimer e_timer;	//Timer
char *reg_service_url = "/registration";	//Resource URL for registration
static bool registration_status = false;
static int device_type = 0;	//0: sensor;	1: actuator


/* Declare and auto-start this file's process */
PROCESS(device_process, "Sensor/Actuator Device");
AUTOSTART_PROCESSES(&device_process);


/*------------------------------------------------------------------------------------------*/

/*	Handler for the response from theserver	*/

void client_chunk_handler(coap_message_t *response) {
 
	const uint8_t *chunk;
	if(response == NULL) { 
		printf("Request timed out\n"); 
		return;
	}

	registration_status = true;

	int len = coap_get_payload(response, &chunk);
	LOG_INFO("RESPONSE LEN: %i\nCONTENT: %s\n", len, chunk); 

}

/*------------------------------------------------------------------------------------------*/

PROCESS_THREAD(device_process, ev, data){

	static coap_endpoint_t server_ep;
  	static coap_message_t request[1];  /* the packet can be treated as pointer */

	PROCESS_BEGIN();

	/*	Get device type from command line and activate the related resources	*/
	while(1){
		PROCESS_WAIT_EVENT_UNTIL(ev == serial_line_event_message);
		if(strcmp(data, "sensor") == 0){
			//No led needed
			device_type = 0;

			/*	Resource activation	*/
			coap_activate_resource(&res_humidity, "humidity");
			coap_activate_resource(&res_temp, "temperature");
			break;
		}
		else if(strcmp(data, "actuator") == 0){
			device_type = 1;

			/*	Resource activation	*/
			coap_activate_resource(&res_sprinkler, "sprinkler");
			coap_activate_resource(&res_light, "light");
			//Activate RED led - sprinkler && light off 
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
			break;
		}
	}

	
	/*	Node Registration	*/

	// Populate the coap_endpoint_t data structure
	coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
	// Prepare the message
	coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
	coap_set_header_uri_path(request, reg_service_url);

	//printf("SERVER_EP %s\nRequest: %s\n",server_ep, request); 
	while(!registration_status){
		LOG_INFO("Sending registration request to the server\n");
		COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
	}

	printf("STATUS: %s\n", registration_status ? "true" : "false");
	etimer_set(&e_timer, CLOCK_SECOND * REQ_INTERVAL);
	
	printf("Periodical request of humidity level\n");

	while(1){

		PROCESS_WAIT_EVENT();
		if(ev == PROCESS_EVENT_TIMER){
		    printf("Event triggered\n");
		  	if(device_type == 0){
				res_humidity.trigger();
				res_temp.trigger();
			}else{
				res_sprinkler.trigger();
				res_light.trigger();
			}
			
			etimer_reset(&e_timer);
		}
	}
  	PROCESS_END();
}
