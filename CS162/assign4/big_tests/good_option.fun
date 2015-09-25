type OptionN = Some num
            | None
            

let mapO = (rec mapInner : ((num) => num, OptionN) => OptionN = 
            (f: (num) => num, o: OptionN) => case o of
                | None => OptionN!None
                | Some(n)  => OptionN!Some(f(n)) in mapInner)
    in mapO(((x: num) => x + 1), OptionN!Some(1))
