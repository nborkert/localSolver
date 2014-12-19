package main

import (
	"bufio"
	"fmt"
	"os"
	"solver"
	"sort"
	"strconv"
)

func main() {
	file, err := os.Open(os.Args[1])

	if err != nil {
		panic(err)
	}

	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)
	allPlayers := make([]solver.Player, 10)
	for scanner.Scan() {
		newLine := scanner.Text()
		player := solver.CreatePlayer(newLine)
		allPlayers = solver.AddPlayerToSingleList(player)
	}

	//Build stacks. Loop over array of players, picking QBs,
	//then relooping and finding WRs on same team
	stacks := make([]solver.PlayerStack, 0)
	for _, player := range allPlayers {
		//fmt.Println("Player = ", player)
		if player.Position == "QB" {
			for _, WR := range allPlayers {
				if WR.Position == "WR" && player.Team == WR.Team {
					stack := solver.PlayerStack{player.PlayerName + " + " + WR.PlayerName, player.Team, player.ProjectedPoints + WR.ProjectedPoints, (player.ProjectedPoints + WR.ProjectedPoints) / float64(player.Salary+WR.Salary), player.Salary + WR.Salary}
					//fmt.Println(stack)
					stacks = append(stacks, stack)
				}
			}
		}
	}
/*
	sort.Sort(solver.ByVal(stacks))
	fmt.Println("Stacks by value:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", value = " + strconv.FormatFloat(stack.Value, 'f', 6, 64) + ", salary = " + strconv.Itoa(stack.Salary))
	}

	sort.Sort(solver.ByPoints(stacks))
	fmt.Println("*************************")
	*/
	fmt.Println("Stacks by projected points:")
	fmt.Println("Names,points,salary")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + "," + strconv.FormatFloat(stack.ProjectedPoints, 'f', 6, 64) + "," + strconv.Itoa(stack.Salary))
	}
}
