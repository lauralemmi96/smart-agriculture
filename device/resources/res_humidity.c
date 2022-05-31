#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "Humidity"
#define LOG_LEVEL LOG_LEVEL_APP

/* Range of values for humidity */
int min_hum_value =	20;
int max_hum_value =	80;
int humidity_value = 50;
static unsigned int get_accept = APPLICATION_JSON;
static unsigned int post_accept = APPLICATION_JSON;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

/*----------------------------------------------------------------------------------------------*/

EVENT_RESOURCE(res_humidity,
         "title=\"Humidity Sensor\";rt=\"humidity\";if=\"sensor\";obs",
	res_get_handler,
        res_post_put_handler,
        res_post_put_handler,
         NULL, 
	res_event_handler);
        
/*----------------------------------------------------------------------------------------------*/

static void res_event_handler(void) {
 
	//Randomly generated humidity value for each observation
    humidity_value = (rand() % (max_hum_value - min_hum_value + 1)) + min_hum_value;
    coap_notify_observers(&res_humidity);
}


/* Humidity sensor: given the area, it returns the humudity percentage detected */


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	
	char response_message[COAP_MAX_CHUNK_SIZE];

	coap_get_header_accept(request, &get_accept);
	//Handle only JSON format
	if(get_accept == APPLICATION_JSON){
		
		coap_set_header_content_format(response, APPLICATION_JSON);
		
		int len = snprintf(response_message, COAP_MAX_CHUNK_SIZE, "{\"humidity\":%d}", humidity_value);
		if(len > 0){
			//Prepare the message
			memcpy(buffer, (uint8_t*)response_message, len);
            coap_set_payload(response, buffer, len);
		}else
			LOG_INFO("Error: Response message not formed\n");
	}else{
		coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
	   	const char *msg = "Supported content-types:application/json";
	    coap_set_payload(response, msg, strlen(msg));
	}
	
	
}


// CHANGE THE MIN AND MAX VALUE OF HUMIDITY WHEN SPRINKLER IS SWITCHED ON/OFF.

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	
	
	if(request == NULL){

		LOG_INFO("[HUM]: Empty request\n");
		return;
	}

	coap_get_header_accept(request, &post_accept);
	//Handle only JSON format
	if(post_accept == APPLICATION_JSON){

		size_t pay_len = 0;
		char *variation_mode = NULL;
		int value = 0;
		bool good_req = false;

		const uint8_t **message;
		message = malloc(request->payload_len);

		if(message == NULL){
			LOG_INFO("[TEMP]: Empty payload\n");
			return;
		}

		pay_len = coap_get_payload(request, message);
		LOG_INFO("Message received: %s\n", (char *)*message);

		
		if(pay_len > 0){
			
			//Splitting the payload
			char *split;

			//Take the variable
			split = strtok((char*)*message, ":");	//	{"increase" / {"decrease"
			const char* start = split + 2;
			const char* end = split + strlen(split)-1;
			size_t size = end - start;

			if(size == 0) {
				LOG_INFO("Size equal to 0.\n");
				return;
			} else {
				variation_mode = malloc(size);
				strncpy(variation_mode, start, size);
				variation_mode[size] = '\0';
			}

			
			
			//Take the value
			split = strtok(NULL, "=");	//	1}

			start = split;
			end = split + strlen(split) - 1;
			size = end - start;
			
			if(size == 0) {
				LOG_INFO("Size equal to 0.\n");
				return;
			} else {
				char *new_value = malloc(size);
				strncpy(new_value, start, size);
				new_value[size] = '\0';
				value = atoi(new_value);
			}

			
			


		}

		LOG_INFO("Variation Type: %s, Value: %d\n", variation_mode, value);
		free(message);
		
		//Check if variable_mode and value are not null
		if(variation_mode != NULL && value != 0){	
			if(strcmp(variation_mode, "increase") == 0) {
				//increase range values
				min_hum_value += value;
				max_hum_value += value;
				
				
				LOG_DBG("Max and Min humidity increased\n");	
		
			}else if(strcmp(variation_mode, "decrease")==0) {
				//decrease range values
				min_hum_value -= value;
				max_hum_value -= value;
				
		
				LOG_DBG("Max and Min humidity decreased\n");
			}
			good_req = true;
			LOG_INFO("New Max: %d, New Min: %d\n", max_hum_value, min_hum_value);

		}
		//send response
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
