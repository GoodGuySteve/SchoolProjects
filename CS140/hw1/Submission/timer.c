/*********************************************************/
/* A simple wall-clock timer.                            */
/*                                                       */
/* Since "gettimeofday" is implemented in different ways */
/* on different systems, this shouldn't be relied on     */
/* for sub-second accuracy.                              */
/*********************************************************/

#include <sys/time.h>

double elapsed_seconds()
{
  struct timeval tv;
  struct timezone tz;
  gettimeofday(&tv, &tz);
  return (double)tv.tv_sec + (double)tv.tv_usec/1000000.0;
}
