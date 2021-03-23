###############################################################################
# Find Realsense sdk
#
#     find_package(Realsense)
#
# Variables defined by this module:
#
#  REALSENSE_FOUND               True if Realsense sdk was found
#  REALSENSE_INCLUDE_DIRS        The location(s) of Realsense sdk headers
#  REALSENSE_LIBRARIES           Libraries needed to use Realsense


FIND_PATH(
        REALSENSE_INCLUDE_DIR rs.h
        /usr/local/include/librealsense2 /usr/include/librealsense2 $ENV{REALSENSE_INCLUDE} ${REALSENSE_INCLUDE}
)
FIND_LIBRARY(
        REALSENSE_LIBRARY realsense2
        /usr/local/lib /usr/lib $ENV{REALSENSE_LIBRARY}  ${REALSENSE_LIBRARY}
)

set(REALSENSE_INCLUDE_DIRS ${REALSENSE_INCLUDE_DIR})
set(REALSENSE_LIBRARIES ${REALSENSE_LIBRARY})

IF (REALSENSE_INCLUDE_DIR AND REALSENSE_LIBRARY)
    message("REALSENSE_INCLUDE_DIRS and REALSENSE_LIBRARY found")
    message("Realsense lib -> " ${REALSENSE_LIBRARY})
    set(REALSENSE_FOUND TRUE)
    include_directories(${REALSENSE_INCLUDE_DIRS})

ELSE(REALSENSE_INCLUDE_DIR AND REALSENSE_LIBRARY)
    message("REALSENSE_INCLUDE_DIR AND REALSENSE_LIBRARY not found")
ENDIF (REALSENSE_INCLUDE_DIR AND REALSENSE_LIBRARY)