#include <fstream>
#include <cstdlib>

#ifdef __cplusplus
extern "C" {
#endif


typedef struct MyFileLogger MyFileLogger;

MyFileLogger* newMyFileLogger()
{
	return new MyFileLogger();
}

/*class MyFileLogger: public libfreenect2::Logger
{
private:
  std::ofstream logfile_;
public:*/
  MyFileLogger(MyFileLogger* v, const char *filename);
   /* if (filename)
      logfile_.open(filename);
    level_ = Debug;*/

  int good(MyFileLogger* v)
{
	return v->good();
}
   /* return logfile_.is_open() && logfile_.good(); not sure if i have to define functions here if theyre defined in c already*/
  void log(MyFileLogger* v, Level level, const std::string &message){
	return v->log();
	}



#ifdef __cplusplus
}
#endif
#endif
