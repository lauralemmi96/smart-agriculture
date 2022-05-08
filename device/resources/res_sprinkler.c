#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "os/dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Sprinkler"
#define LOG_LEVEL LOG_LEVEL_APP

bool sprinkler_status = false;
extern bool light_status;
static unsigned int get_accept = APPLICATION_JSON;
static unsigned int post_accept = APPLICATION_JSON;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_sprinkler,
         "title=\"Sprinkler Actuator\";rt=\"sprinkler\";if=\"actuator\";obs",
	res_get_handler,
         res_post_put_handler,
         res_post_put_handler,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
    coap_notify_observers(&res_sprinkler);
}


/* Sprinkler Activator: it returns its status */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];

	coap_get_header_accept(request, &get_accept);
	if(get_accept == APPLICATION_JSON){
		
		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"sprinkler\":%s}", sprinkler_status ? "ON" : "OFF");
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
	//coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "SPRINKLER STATUS: %s\n", sprinkler_status ? "ON" : "OFF"));
	

}


/* Sprinkler Activator: it changes its status, and the leds accordingly RED: OFF, GREEN: ON */

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	if(request == NULL){

			LOG_INFO("[SPRINKLER]: Empty request\n");
			return;
		}



	coap_get_header_accept(request, &post_accept);
	
	if(post_accept == APPLICATION_JSON){
		
		size_t pay_len = 0;
		char *new_status = NULL;
		char *var = NULL;
		bool good_req = false;

		const uint8_t **message;
		message = malloc(request->payload_len);

		if(message == NULL){
			LOG_INFO("[SPRINKLER]: Empty payload\n");
			return;
		}


		pay_len = coap_get_payload(request, message);
		LOG_INFO("Message received: %s\n", (char *)*message);

	
		if(pay_len > 0){
		
			//Splitting the payload
			char *split;
			

			//Take the variable
			split = strtok((char*)*message, ":");	//	{"status"
			const char* start = split + 2;
			const char* end = split + strlen(split)-1;
			size_t size = end - start;

			if(size == 0) {
				LOG_INFO("Size equal to 0.\n");
				return;
			} else {
				var = malloc(size);
				strncpy(var, start, size);
				var[size] = '\0';
			}

		
			//Take the value
			split = strtok(NULL, ":");	//"ON"} or "OFF"}
			start = split + 1;
			end = split + strlen(split) - 2;
			size = end - start;
			
			if(size == 0) {
				LOG_INFO("Size equal to 0.\n");
				return;
			} else {
				new_status = malloc(size);
				strncpy(new_status, start, size);
				new_status[size] = '\0';
			}

			

		}

		LOG_INFO("VAR: %s, STATUS: %s\n", var, new_status);

		
		if(var != NULL && new_status != NULL){	
			if(strcmp(new_status, "ON") == 0 && !sprinkler_status) {

				sprinkler_status = true;

				//check light status
				// (ON => GREEN(sprinkler) + YELLOW (light))
				// (OFF => RED(light) + GREEN (sprinkler))

				if(light_status)
					leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN) | LEDS_NUM_TO_MASK(LEDS_YELLOW));
				else
					leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN) | LEDS_NUM_TO_MASK(LEDS_RED));
				
				LOG_DBG("Sprinkler switched ON\n");		
			}else if(strcmp(new_status, "OFF") == 0 && sprinkler_status) {

				sprinkler_status = false;

				//check light status
				// (ON => RED(sprinkler) + YELLOW (light))
				// (OFF => RED)
				if(light_status)
					leds_set(LEDS_NUM_TO_MASK(LEDS_RED) | LEDS_NUM_TO_MASK(LEDS_YELLOW));
				else
					leds_set(LEDS_NUM_TO_MASK(LEDS_RED));

				LOG_DBG("Sprinkler switched OFF\n");
			}
			good_req = true;	
		}

		if(good_req)
			coap_set_status_code(response, CHANGED_2_04);

		if(!good_req)
			coap_set_status_code(response, BAD_REQUEST_4_00);		
		
	}else{
		coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
	   	const char *msg = "Supported content-types:application/json";
	    	coap_set_payload(response, msg, strlen(msg));
	}


}
