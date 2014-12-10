package main

import (
	"bufio"
	"fmt"
	"os"
	"solver"
	"sort"
	"strconv"
)

type PlayerStack struct {
	PlayerNames     string
	ProjectedPoints float64
	Value           float64
}

type ByVal []PlayerStack

func (this ByVal) Len() int {
	return len(this)
}

func (this ByVal) Less(i, j int) bool {
	return this[i].Value > this[j].Value
}

func (this ByVal) Swap(i, j int) {
	this[i], this[j] = this[j], this[i]
}

type ByPoints []PlayerStack

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
	stacks := make([]PlayerStack, 0)
	for idx, player := range allPlayers {
		for idxInner, stackedPlayer := range allPlayers {
			if idxInner <= idx {
				continue
			}
			if player.Team != stackedPlayer.Team {
				stack := PlayerStack{player.PlayerName + " + " + stackedPlayer.PlayerName, player.ProjectedPoints + stackedPlayer.ProjectedPoints, (player.ProjectedPoints + stackedPlayer.ProjectedPoints) / float64(player.Salary+stackedPlayer.Salary)}
				//fmt.Println(stack)
				stacks = append(stacks, stack)
			}
		}
	}

	sort.Sort(ByVal(stacks))

	fmt.Println("Stacks by value:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", value = " + strconv.FormatFloat(stack.Value, 'f', 6, 64))
	}

	sort.Sort(ByPoints(stacks))
	fmt.Println("****************************")
	fmt.Println("Stacks by projected points:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", projected points = " + strconv.FormatFloat(stack.ProjectedPoints, 'f', 6, 64))
	}
}
