type Either['A, 'B] = Left 'A | Right 'B

let map = ['A, 'B](fa: ('A) => 'A, fb: ('B) => 'B, e: Either<'A, 'B>) =>
    case e of
    | Left(a) => Either!Left<'A, 'B>(fa(a))
    | Right(b) => Either!Right<'A, 'B>(fb(b))
in map<num, bool>((n: num) => n + 1, (b: bool) => !b, Either!Right<num, bool>(true))
