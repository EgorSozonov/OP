cmake_minimum_required(VERSION 3.13)
project(O7)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "../_bin")
set(CMAKE_BUILD_TYPE "Debug")


set(CMAKE_C_FLAGS "-std=c11")

include_directories(${PROJECT_SOURCE_DIR})

add_executable(op main.c runtime/Runtime.c types/Types.h utils/Arena.c utils/Either.h utils/EitherHeader.h utils/Stack.h utils/StackHeader.h utils/String.c)
#add_subdirectory(utils)
#add_subdirectory(types)
#add_subdirectory(runtime)

target_link_libraries(op)
