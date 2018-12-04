//
// OIFrame.hpp created by Haotao Lai on 2018-08-09.
//
// Modified to a c header by Cameron Belcher & Jacob Biederman 2018-12-04
//

#ifndef OPENISS_OIFRAME_H
#define OPENISS_OIFRAME_H

#include "OIEnum.h"
 
typedef struct openissStruct { 
/*attempt to create a namespace in C */

struct OISkeleton;

/*This struct is also useless except as a template b/c it's virtual we think */
struct OIFrame {
	virtual ~OIFrame() = default;

	virtual int getHeight() const = 0;
	virtual int getWidth() const = 0;
	virtual void save(const char *fileName) = 0;
	virtual void show(const char *winName) = 0;
	virtual void drawSkeleton(OISkeleton *pSkeleton, OpenISS_OIEnum::JointType *types) = 0;
};


/* This whole class is impossible in C b/c of inheritance so we're not sure what to do with it.
 struct OIAbstractDataFrame : public OIFrame {
	~OIAbstractDataFrame() override = default;
	virtual void *getData() = 0;
	virtual int getBytesPerPixel() = 0;
}; */

} openiss; // end of namespace
#endif //OPENISS_OIFRAME_H
