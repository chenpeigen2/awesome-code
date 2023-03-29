package main

import (
	"fmt"
	"strconv"
	"strings"
)

type TreeNode struct {
	Val   int
	Left  *TreeNode
	Right *TreeNode
}

type Codec struct {
}

func Constructor() (_ Codec) {
	return
}

// Serializes a tree to a single string.
func (this *Codec) serialize(root *TreeNode) string {
	var ans []string
	var preOrder func(*TreeNode)
	preOrder = func(node *TreeNode) {
		if node != nil {
			ans = append(ans, strconv.Itoa(node.Val))
			preOrder(node.Left)
			preOrder(node.Right)
		}
	}
	preOrder(root)
	return strings.Join(ans, ",")
}

// Deserializes your encoded data to tree.
func (this *Codec) deserialize(data string) *TreeNode {
	if data == "" {
		return nil
	}
	vals := strings.Split(data, ",")
	var dfs func(left, right int) *TreeNode
	dfs = func(left, right int) *TreeNode {
		if len(vals) == 0 {
			return nil
		}
		val, _ := strconv.Atoi(vals[0])
		if val < left || val > right {
			return nil
		}
		// 利用条件二叉搜索树
		vals = vals[1:]
		node := &TreeNode{val, nil, nil}
		node.Left = dfs(left, val)
		node.Right = dfs(val, right)
		return node
	}
	return dfs(-1, 10007)
}

//https://leetcode.cn/problems/delete-columns-to-make-sorted/
func minDeletionSize(strs []string) int {
	n := len(strs)
	m := len(strs[0])
	ans := 0
	for i := 0; i < m; i++ {
		for j := 1; j < n; j++ {
			if strs[j][i] < strs[j-1][i] {
				ans += 1
				break
			}
		}
	}
	return ans
}

type MyType struct {
	field string
}

func main() {
	var ans []string
	fmt.Println(len(ans))
	fmt.Println(cap(ans))

	fmt.Println("========")
	ret := []int8{1, 2, 3, 4}
	for r := range ret {
		fmt.Println(r)
	}

	var array [10]MyType

	for i, _ := range array {
		if i == 1 {
			array[i].field = "catched"
		} else {
			array[i].field = "foo"
		}
	}

	for _, e := range array {
		fmt.Println(e.field)
	}

	str := "abc"

	fmt.Println(str[0])
}
