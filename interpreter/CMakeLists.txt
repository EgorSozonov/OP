cmake_minimum_required(VERSION 3.13)
project(O7)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "~/Exes/OP")
set(CMAKE_BUILD_TYPE "Debug")


set(CMAKE_C_FLAGS "-std=c11")

include_directories(${PROJECT_SOURCE_DIR})

add_executable(op main.c)
#add_subdirectory(core)

target_link_libraries(op)
