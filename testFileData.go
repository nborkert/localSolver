package main

import (
	"bufio"
	"os"
	"fmt"
	"solver"
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
		newLine := scanner.Text()
		fmt.Println(newLine)
		solver.AddPlayer(newLine)
	}
}
