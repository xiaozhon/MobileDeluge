/*
 * $Date: 2014-09-05 12:56:41 $
 * @author Xiaoyang Zhong
 */
 
generic configuration InternalVoltageC() {
  provides interface Read<uint16_t>;
  provides interface ReadStream<uint16_t>;

  provides interface Resource;
  provides interface ReadNow<uint16_t>;
}
implementation {
  components new AdcReadClientC();
  Read = AdcReadClientC;

  components new AdcReadStreamClientC();
  ReadStream = AdcReadStreamClientC;

  components new AdcReadNowClientC();
  Resource = AdcReadNowClientC;
  ReadNow = AdcReadNowClientC;
 
//  components Msp430InternalVoltageP;
//  AdcReadClientC.AdcConfigure -> Msp430InternalVoltageP;
//  AdcReadStreamClientC.AdcConfigure -> Msp430InternalVoltageP;
//  AdcReadNowClientC.AdcConfigure -> Msp430InternalVoltageP;
  
  components SensorSettingsC as Settings;

  AdcReadClientC.AdcConfigure -> Settings.AdcConfigure[VOLT];
  AdcReadStreamClientC.AdcConfigure -> Settings.AdcConfigure[VOLT];
  AdcReadNowClientC.AdcConfigure -> Settings.AdcConfigure[VOLT];
  
}
