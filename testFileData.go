package main

import (
	"bufio"
	"os"
	"fmt"
	"solver"
)

func main() {
	file, err := os.Open("/Users/ndborkedaunt/Downloads/fanduel/simpleTestFile.txt")
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
//		fmt.Printf("Added player\n")
	}
	allPlayers := solver.CreatePlayersArrays()
	if allPlayers == nil {
		fmt.Println("ERROR")
	}
//	fmt.Printf("All Players = %v\n", allPlayers)
	winningRoster := solver.CreateRosters()
	fmt.Printf("Winning roster is %v\n", winningRoster)
}
