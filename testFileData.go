package main

import (
	"bufio"
	"fmt"
	"os"
	"solver"
	//	"time"
	//	"runtime"
	"strconv"
)

func main() {

	//	runtime.GOMAXPROCS(runtime.NumCPU())

	//	fmt.Printf("CPUS = %v\n", runtime.NumCPU())

	file, err := os.Open(os.Args[1])

	if err != nil {
		panic(err)
	}

	defer file.Close()
	var minPoints float64
	minPoints, err = strconv.ParseFloat(os.Args[2], 64)
	if err != nil {
		fmt.Println("Usage: testFileData <inputFile> <minPoints>")
		fmt.Println("If <minPoints> is omitted, a value of 0.0 will be used for minPoints")
		minPoints = 0.0
	}

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
	//fmt.Println(allPlayers)
	//	startTime := time.Now()
	winningRoster := solver.CreateRosters(minPoints)

	if winningRoster == nil {
		fmt.Println("ERROR reading winning Roster")
	}

	//	winningRoster := solver.CreateSimplexRoster()
	//	elapsed := time.Since(startTime)
	//	winningPoints := solver.PointsForRoster(winningRoster)
	//	fmt.Printf("Winning roster is %v\n", winningRoster)
	//	fmt.Printf("Winning points total is %v\n", winningPoints)
	//	fmt.Printf("Winning roster salary is %v\n", solver.RosterSalary(winningRoster))
	//	fmt.Printf("Time required to find winning roster = %v\n", elapsed)
}
