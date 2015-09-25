type Tree = Leaf num
            | Node (| value:num, left:Tree, right:Tree |)
rec fold:((num,num)=>num, num, Tree)=>num = (fun:(num, num)=>num, acc:num, tree:Tree) =>
    case tree of
    | Leaf(x) => fun(acc, x)
    | Node(rcd) =>
        let left  = fold(fun, acc, rcd.left) in
        let right = fold(fun, left, rcd.right) in
        fun(right, rcd.value)
in
  fold( (a:num, b:num) => a+b, 0, Tree!Node((| value:1, left:Tree!Leaf(2), right:Tree!Leaf(3) |)))