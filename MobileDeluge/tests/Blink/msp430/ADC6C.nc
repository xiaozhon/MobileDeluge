/*
 * $Date: 2014-05-01 12:56:41 $
 * @author Yimei Li
 */


#include <sensors.h>
generic configuration ADC6C()
{
  provides {
    interface Read<uint16_t> as Read;
    interface ReadNow<uint16_t> as ReadNow;
    interface Resource as ReadNowResource;
  }
}
implementation
{
  components SensorSettingsC as Settings;
             
  components new AdcReadClientC() as AdcReadClient;
  Read = AdcReadClient;
  AdcReadClient.AdcConfigure -> Settings.AdcConfigure[ADC6];
  
  components new AdcReadNowClientC() as AdcReadNowClient;
  ReadNow = AdcReadNowClient;
  ReadNowResource = AdcReadNowClient;
  AdcReadNowClient.AdcConfigure -> Settings.AdcConfigure[ADC6];
}

