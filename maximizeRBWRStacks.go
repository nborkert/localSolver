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
	RBfile, err := os.Open(os.Args[1])

	if err != nil {
		panic(err)
	}

	defer RBfile.Close()

	scanner := bufio.NewScanner(RBfile)
	scanner.Split(bufio.ScanLines)
	allRBs := make([]solver.Player, 1)
	for scanner.Scan() {
		newLine := scanner.Text()
		player := solver.CreatePlayer(newLine)
		allRBs = solver.AddPlayerToSingleList(player)
	}

	WRfile, err := os.Open(os.Args[2])

	if err != nil {
		panic(err)
	}

	defer WRfile.Close()

	WRscanner := bufio.NewScanner(WRfile)
	WRscanner.Split(bufio.ScanLines)
	allWRs := make([]solver.Player, 1)
	for WRscanner.Scan() {
		newLine := WRscanner.Text()
		player := solver.CreatePlayer(newLine)
		allWRs = solver.AddPlayerToWRList(player)
	}

	//Build stacks. Loop over array of players,
	stacks := make([]solver.PlayerStack, 0)
	for _, RB := range allRBs {
		for _, WR := range allWRs {
			stack := solver.PlayerStack{RB.PlayerName + " + " + WR.PlayerName, RB.Team + ":" + WR.Team, RB.ProjectedPoints + WR.ProjectedPoints, (RB.ProjectedPoints + WR.ProjectedPoints) / float64(RB.Salary+WR.Salary), RB.Salary + WR.Salary}
				//fmt.Println(stack)
				stacks = append(stacks, stack)
		}
	}
/*
	sort.Sort(solver.ByVal(stacks))

	fmt.Println("Stacks by value:")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + ", " + stack.Team + ", value = " + strconv.FormatFloat(stack.Value, 'f', 6, 64) + ", salary = " + strconv.Itoa(stack.Salary))
	}
*/
	sort.Sort(solver.ByPoints(stacks))
//	fmt.Println("****************************")
	fmt.Println("Stacks by projected points:")
	fmt.Println("Names,teams,points,salary")
	for _, stack := range stacks {
		fmt.Println(stack.PlayerNames + "," + stack.Team + "," + strconv.FormatFloat(stack.ProjectedPoints, 'f', 6, 64) + "," + strconv.Itoa(stack.Salary))
	}
}
