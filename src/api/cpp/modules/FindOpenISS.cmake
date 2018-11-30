###############################################################################
# Find OpenISS
#
#     find_package(OpenISS)
#
# Variables defined by this module:
#
#  OPENISS_FOUND               True if OpenISS was found
#  OPENISS_INCLUDE_DIRS        The location(s) of OpenISS headers
#  OPENISS_LIBRARIES           Libraries needed to use OpenISS


FIND_PATH(
    OPENISS_INCLUDE_DIR OpenISS.h
    /usr/local/include/openiss /usr/include/openiss $ENV{OPENISS_INCLUDE} ${OPENISS_INCLUDE}
    "${PROJECT_BINARY_DIR}/include"
)
FIND_LIBRARY(
    OPENISS_LIBRARY openiss
    /usr/local/lib/openiss /usr/lib/openiss $ENV{OPENISS_LIBRARY}  ${OPENISS_LIBRARY}
    "${PROJECT_BINARY_DIR}/lib"
)

set(OPENISS_INCLUDE_DIRS ${OPENISS_INCLUDE_DIR})
set(OPENISS_LIBRARIES ${OPENISS_LIBRARY})

IF (OPENISS_INCLUDE_DIR AND OPENISS_LIBRARY)
    message("OPENISS_INCLUDE_DIRS and OPENISS_LIBRARY found")
    message("openiss lib -> " ${OPENISS_LIBRARY})
    set(OPENISS_FOUND TRUE)
    include_directories(${OPENISS_INCLUDE_DIRS})

ELSE(OPENISS_INCLUDE_DIR AND OPENISS_LIBRARY)
    message("OPENISS_INCLUDE_DIR AND OPENISS_LIBRARY not found")
ENDIF (OPENISS_INCLUDE_DIR AND OPENISS_LIBRARY)
#message("= = = = = = = = = = = = = = = = = = = = = = = = ")