/**
 * The DisseminationC component, simply provides StdControl interface.
 *
 * @author Xiaoyang Zhong <xiaozhon@iupui.edu>
 * @version $Revision: 1.0 $ $Date: 2014-02-28 22:56:21 $
 */
#include "SimpleDrip.h"

configuration DisseminationC {
	provides interface StdControl;
}

implementation {
	components DripStateP;
	StdControl = DripStateP;
}

