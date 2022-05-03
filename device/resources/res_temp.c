#include "contiki.h"
#include "coap-engine.h"
#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Temperature"
#define LOG_LEVEL LOG_LEVEL_APP

int min_temp_value =	5;
int max_temp_value =	25;
int temp_value = 10;
static unsigned int accept = -1;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_temp,
         "title=\"Temperature Sensor\";rt=\"temperature\";if=\"sensor\";obs",
	res_get_handler,
         NULL,
         NULL,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    coap_notify_observers(&res_temp);
}


/* Temperature sensor: given the area, it returns the humudity percentage detected */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];
	temp_value = (rand() % (max_temp_value - min_temp_value + 1)) + min_temp_value;

	coap_get_header_accept(request, &accept);
	
	if(accept == APPLICATION_JSON){

		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"temperature\":%d}", temp_value);
		if(len > 0){
			memcpy(buffer, (uint8_t*)response_message, len);
            		coap_set_payload(response, buffer, len); 

		}else
			LOG_INFO("Error: Response message not formed\n");
	}else{
		coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
	   	const char *msg = "Supported content-types:application/json";
	    	coap_set_payload(response, msg, strlen(msg));
	}
	
	//coap_set_header_content_format(response, TEXT_PLAIN);
	//coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "TEMPERATURE: %i\n", temp_value));
	

}
