package main

import (
	"bufio"
	"fmt"
	"os"
	"solver"
	"sort"
)

type QBWRStack struct {
	PlayerNames string
	Team        string
	Value       float64
}

type ByVal []QBWRStack

func (this ByVal) Len() int {
	return len(this)
}

func (this ByVal) Less(i, j int) bool {
	return this[i].Value > this[j].Value
}

func (this ByVal) Swap(i, j int) {
	this[i], this[j] = this[j], this[i]
}

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
	stacks := make([]QBWRStack, 0)
	for _, player := range allPlayers {
		//fmt.Println("Player = ", player)
		if player.Position == "QB" {
			for _, WR := range allPlayers {
				if WR.Position == "WR" && player.Team == WR.Team {
					stack := QBWRStack{player.PlayerName + WR.PlayerName, player.Team, (player.ProjectedPoints + WR.ProjectedPoints) / float64(player.Salary+WR.Salary)}
					//fmt.Println(stack)
					stacks = append(stacks, stack)
				}
			}
		}
	}

	sort.Sort(ByVal(stacks))

	for _, stack := range stacks {
		fmt.Println(stack)
	}
}
