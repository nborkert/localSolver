package main

import (
	"bufio"
	"fmt"
	"os"
	"solver"
	"sort"
	"strconv"
)

type QBWRStack struct {
	PlayerNames     string
	Team            string
	ProjectedPoints float64
	Value           float64
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

type ByPoints []QBWRStack

func (this ByPoints) Len() int {
	return len(this)
}

func (this ByPoints) Less(i, j int) bool {
	return this[i].ProjectedPoints > this[j].ProjectedPoints
}

func (this ByPoints) Swap(i, j int) {
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
					stack := QBWRStack{player.PlayerName + " + " + WR.PlayerName, player.Team, player.ProjectedPoints + WR.ProjectedPoints, (player.ProjectedPoints + WR.ProjectedPoints) / float64(player.Salary+WR.Salary)}
					//fmt.Println(stack)
					stacks = append(stacks, stack)
				}
			}
		}
	}

	sort.Sort(ByVal(stacks))
	fmt.Println("Stacks by value:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", value = " + strconv.FormatFloat(stack.Value, 'f', 6, 64))
	}

	sort.Sort(ByPoints(stacks))
	fmt.Println("*************************")
	fmt.Println("Stacks by projected points:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", projected points = " + strconv.FormatFloat(stack.ProjectedPoints, 'f', 6, 64))
	}
}
