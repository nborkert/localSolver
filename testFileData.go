package main

import (
	"bufio"
	"os"
	"fmt"
)

func main() {
	file, err := os.Open("/Users/ndborkedaunt/Downloads/fanduel/testFile.txt")
	if err != nil {
		panic(err)
	}

	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		fmt.Println(scanner.Text())
	}
}
