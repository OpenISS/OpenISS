set(FRAMEWORK_SRC ${PROJECT_SOURCE_DIR}/core)
message("Framework CPP API Sources at: " ${FRAMEWORK_SRC})

set(HEADER_FILES ${PROJECT_SOURCE_DIR}/include)
message("Framework CPP API Headers at: " ${HEADER_FILES})

set(ADAPTER_FILES ${PROJECT_SOURCE_DIR}/adapters)
message("Framework CPP API Adapters at: " ${ADAPTER_FILES})

aux_source_directory(. SOURCEFILES)

add_library(openiss ${SOURCEFILES})

set_target_properties(openiss
        PROPERTIES
        ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
        )

find_package(OpenNI2 REQUIRED)
find_package(NiTE2 REQUIRED)
find_package(GLUT REQUIRED)
find_package(OpenGL REQUIRED)
find_package(OpenCV REQUIRED)
find_package(Realsense REQUIRED)

if(NITE2_FOUND)
    target_link_libraries(openiss ${NITE2_LIBRARIES})
    #message("link to nite2 libs successfully -> " ${NITE2_LIBRARIES})
endif()

if(OPENNI2_FOUND)
    target_link_libraries(openiss ${OPENNI2_LIBRARIES})
    #message("link to openni2 libs successfully -> " ${OPENNI2_LIBRARIES})
endif()

if(GLUT_FOUND)
    target_link_libraries(openiss ${GLUT_LIBRARIES})
    #message("link to glut libs successfully -> " ${GLUT_LIBRARIES})
endif()

if(OPENGL_FOUND)
    target_link_libraries(openiss ${OPENGL_LIBRARIES})
    #message("link to opengl libs successfully -> " ${OPENGL_LIBRARIES})
endif()

if(OpenCV_FOUND)
    include_directories(${OpenCV_INCLUDE_DIR})
    target_link_libraries(openiss ${OpenCV_LIBS})
    message("************************** OpenCV_INCLUDE_DIR at: " ${OpenCV_INCLUDE_DIR})
    #message("link to opencv libs successfully -> " ${OpenCV_LIBS})
endif()

if(REALSENSE_FOUND)
    include_directories(${REALSENSE_INCLUDE_DIRS})
    target_link_libraries(openiss ${REALSENSE_LIBRARIES})
    #message("link to realsense libs successfully -> " ${REALSENSE_LIBRARIES})
endif()