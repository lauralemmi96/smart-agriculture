#include "contiki.h"
#include "coap-engine.h"
#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Humidity"
#define LOG_LEVEL LOG_LEVEL_APP

int min_hum_value =	20;
int max_hum_value =	80;
int humidity_value = 50;
static unsigned int accept = -1;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_humidity,
         "title=\"Humidity Sensor\";rt=\"humidity\";if=\"sensor\";obs",
	res_get_handler,
         NULL,
         NULL,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    humidity_value = (rand() % (max_hum_value - min_hum_value + 1)) + min_hum_value;
    coap_notify_observers(&res_humidity);
}


/* Humidity sensor: given the area, it returns the humudity percentage detected */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];

	coap_get_header_accept(request, &accept);
	if(accept == APPLICATION_JSON){
		
		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"humidity\":%d}", humidity_value);
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
	//coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "HUMIDITY: %i\n", humidity_value));
	

}
