type List['A] = Cons (| hd: 'A, tl:List<'A> |)
              | Nil

rec polyMap:['A,'B](('A) => 'B, List<'A>) => List<'B> =
      ['A, 'B](f: ('A) => 'B, xs:List<'A>) =>
        case xs of
        | Nil => List!Nil<'B>
        | Cons(r) => List!Cons<'B>((| hd:f(r.hd), tl:polyMap<'A,'B>(f, r.tl) |))
in
let list = List!Cons<num>( (| hd:1, tl: List!Nil<num> |) ) in
polyMap<num, num>( (n:num) => n+1, list )
