
/**
 * Read the state of simple drip
 *
 * @author Xiaoyang Zhong
 *
 * @date   Mar 2 2014
 */ 


interface DripState {

  /**
   * @return A bool variable to indicate whether drip is running
   */
  command bool isRunning();
  
  /**
   * Set state
   * 
   */
  command void set(uint16_t newState);

}



