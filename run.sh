#!/bin/bash
docker run --network=springboot-mysql-net --name take-container -p 6000:6000 -p 5000:5000  -v
"C:\Users\Giorgos\Desktop\Coding\Projects\Tale All Versions\take3 all\take3_backend\take3\public:/public" -d take-image



