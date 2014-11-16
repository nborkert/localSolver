package main

import (
	"bufio"
	"os"
	"fmt"
	"solver"
	"time"
	"runtime"
)

func main() {
	runtime.GOMAXPROCS(runtime.NumCPU())
	fmt.Printf("CPUS = %v\n", runtime.NumCPU())

//	file, err := os.Open("/Users/ndborkedaunt/Downloads/fanduel/simpleTestFile.txt")

	//file, err := os.Open("/Users/ndborkedaunt/Downloads/fanduel/output_top5pts.txt")


//	file, err := os.Open("/Users/ndborkedaunt/Downloads/fanduel/dirt.txt")

	file, err := os.Open("/Users/ndborkedaunt/Downloads/google-compute-engine/in.txt")

	if err != nil {
		panic(err)
	}

	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		newLine := scanner.Text()
//		fmt.Println(newLine)
		player := solver.CreatePlayer(newLine)
//		fmt.Printf("Created player %v\n", player)
		solver.AddPlayerToPopulation(player)
	}
	allPlayers := solver.CreatePlayersArrays()
	if allPlayers == nil {
		fmt.Println("ERROR")
	}
	startTime := time.Now()
	winningRoster := solver.CreateRosters()
	elapsed := time.Since(startTime)
	fmt.Printf("Winning roster is %v\n", winningRoster)
	fmt.Printf("Time required to find winning roster = %v\n", elapsed)
}
