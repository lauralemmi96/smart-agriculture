# SmartAgriculture Project for IoT course
CoAP application for vegetable garden management. 


# How to run
Place the smart-agriculture directory in contiki/example/
1) Import the maven project. It must be placed in contiki-ng/examples/smart-agriculture


<space>contiki-ng/examples/smart-agriculture <br />
&nbsp;&nbsp;&nbsp;| <br />
&nbsp;&nbsp;&nbsp;| <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- device <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- rpl-border-router <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- it.unipi.iot <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-- src/main/java/it/unipi/iot <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-- target <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- simulation.csc <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- app.sh <br />
&nbsp;&nbsp;&nbsp;&nbsp;-- br.sh <br />
            

3) Deploy the network (simulation.csc) in Cooja simulator
4) Run the border router (./br)
5) Run the application (./app)
