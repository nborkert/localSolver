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
	allPlayers := make([]solver.Player, 1)
	for scanner.Scan() {
		newLine := scanner.Text()
		player := solver.CreatePlayer(newLine)
		allPlayers = solver.AddPlayerToSingleList(player)
	}

	//Build stacks. Loop over array of players,
	//then relooping and finding another RB or WR
	//but from another team.
	//Input file should simply list all players once.
	stacks := make([]solver.PlayerStack, 0)
	for idx, player := range allPlayers {
		for idxInner, stackedPlayer := range allPlayers {
			if idxInner <= idx {
				continue
			}
			if player.Team != stackedPlayer.Team {
				stack := solver.PlayerStack{player.PlayerName + " + " + stackedPlayer.PlayerName, player.Team + "," + stackedPlayer.Team, player.ProjectedPoints + stackedPlayer.ProjectedPoints, (player.ProjectedPoints + stackedPlayer.ProjectedPoints) / float64(player.Salary+stackedPlayer.Salary)}
				//fmt.Println(stack)
				stacks = append(stacks, stack)
			}
		}
	}

	sort.Sort(solver.ByVal(stacks))

	fmt.Println("Stacks by value:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", " + stack.Team + ", value = " + strconv.FormatFloat(stack.Value, 'f', 6, 64))
	}

	sort.Sort(solver.ByPoints(stacks))
	fmt.Println("****************************")
	fmt.Println("Stacks by projected points:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", " + stack.Team + ", projected points = " + strconv.FormatFloat(stack.ProjectedPoints, 'f', 6, 64))
	}
}
