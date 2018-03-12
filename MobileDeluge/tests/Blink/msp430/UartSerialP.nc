module UartSerialP
{
//  provides interface StdControl;
  provides interface Resource;
  provides interface Msp430UartConfigure;
  uses interface Resource as SubResource;
}

implementation
{

  msp430_uart_union_config_t msp430_uart_1200_config = {
    {
      utxe : 1,
      urxe : 1,
      ubr : UBR_1MHZ_1200,
      umctl : UMCTL_1MHZ_1200,
      ssel : 0x02,
      pena : 0,
      pev : 0,
      spb : 0,
      clen : 1,
      listen : 0,
      mm : 0,
      ckpl : 0,
      urxse : 0,
      urxeie : 1,
      urxwie : 0,
      utxe : 1,
      urxe : 1
    }
  };
/*
  command error_t StdControl.start() {
    return call Resource.immediateRequest();
  }

  command error_t StdControl.stop() {
    call Resource.release();
    
    #if defined(PRINTF_ENABLED)  
	  	printf("\n+++++++++++++ UartSerial: Resource released\n");
	  	printfflush();
	#endif
    return SUCCESS;
  }
  event void Resource.granted() { 
  	 #if defined(PRINTF_ENABLED)  
	  	printf("\n+++++++++++++ UartSerial: Resource granted\n");
	  	printfflush();
	#endif
  }
*/

  async command error_t Resource.immediateRequest() {
    return call SubResource.immediateRequest();
  }

  async command error_t Resource.request() {
    return call SubResource.request();
  }

  async command bool Resource.isOwner() {
    return call SubResource.isOwner();
  }

  async command error_t Resource.release() {
    return call SubResource.release();
  }
 
  event void SubResource.granted() {
    signal Resource.granted();
  }
  
  async command msp430_uart_union_config_t* Msp430UartConfigure.getConfig() {
    return &msp430_uart_1200_config;
  }

}
