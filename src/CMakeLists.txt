cmake_minimum_required(VERSION 3.13)

project(O7)

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "~/Exes/OP")

include_directories(${PROJECT_SOURCE_DIR})

add_executable(app main.c)
add_subdirectory(core)

target_link_libraries(app)
