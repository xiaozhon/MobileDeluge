configuration UartSerialC
{
//  provides interface StdControl;
  provides interface Resource;
  provides interface UartStream;
}

implementation
{
  components new Msp430Uart0C() as UartC;
  UartStream = UartC;

  components UartSerialP;
//  StdControl = UartSerialP;
  Resource = UartSerialP;
  UartSerialP.Msp430UartConfigure <- UartC.Msp430UartConfigure;
  UartSerialP.SubResource -> UartC.Resource;

}
