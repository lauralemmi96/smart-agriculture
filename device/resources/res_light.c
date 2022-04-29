#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "os/dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Light"
#define LOG_LEVEL LOG_LEVEL_APP

bool light_status = false;
extern bool sprinkler_status;
static unsigned int accept = -1;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_light,
         "title=\"Light Actuator\";rt=\"light\";if=\"actuator\";obs",
	res_get_handler,
         res_post_put_handler,
         res_post_put_handler,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    coap_notify_observers(&res_light);
}


/* Light Actuator: it returns its status */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];

	coap_get_header_accept(request, &accept);
	if(accept == APPLICATION_JSON){
		
		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"Light Status\":%s}", light_status ? "ON" : "OFF");
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
	//coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "LIGHT STATUS: %s\n", light_status ? "ON" : "OFF"));
	

}


/* Sprinkler Activator: it changes its status, and the leds accordingly RED: OFF, GREEN: ON */

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	if(request == NULL){

		LOG_INFO("[LIGHT]: Empty request\n");
		return;
	}

	size_t len = 0;
	const char *new_status = NULL;
	bool good_req = false;

	if((len = coap_get_query_variable(request, "status", &new_status))) {
		
		if(strncmp(new_status, "ON", len) == 0 && !light_status) {
			good_req = true;
			light_status = true;
			
			//check if sprinkler on or off 
			// (ON => GREEN(sprinkler) + YELLOW (light))
			// (OFF => RED(sprinkler) + YELLOW (light))
			if(sprinkler_status)
				leds_set(LEDS_NUM_TO_MASK(LEDS_YELLOW) | LEDS_NUM_TO_MASK(LEDS_GREEN));
			else
				leds_set(LEDS_NUM_TO_MASK(LEDS_YELLOW) | LEDS_NUM_TO_MASK(LEDS_RED));
			
			LOG_DBG("Light switched ON\n");		
		}else if(strncmp(new_status, "OFF", len) == 0 && light_status) {
			good_req = true;
			light_status = false;

			//check if sprinkler on or off 
			// (ON => GREEN(sprinkler) + RED (light))
			// (OFF => RED)
			if(!sprinkler_status)
				leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
			else
				leds_set(LEDS_NUM_TO_MASK(LEDS_RED) | LEDS_NUM_TO_MASK(LEDS_GREEN));
			
			LOG_DBG("Light switched OFF\n");
		}

	}

	if(!good_req)
		coap_set_status_code(response, BAD_REQUEST_4_00);


}
