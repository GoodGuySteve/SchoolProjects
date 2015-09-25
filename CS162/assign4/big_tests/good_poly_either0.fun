type Either['A, 'B] = Left (|l: 'A, r: 'B|) | Right (|l: 'A, r: 'B|)

let map = ['A, 'B](fa: ('A) => 'A, fb: ('B) => 'B, e: Either<'A, 'B>) =>
    case e of
    | Left(record) => Either!Left<'A, 'B>((|l: fa(record.l), r: record.r|))
    | Right(record) => Either!Right<'A, 'B>((|l: record.l, r: fb(record.r)|))
in map<num, bool>((n: num) => n + 1, (b: bool) => true, Either!Left<num, bool>((|l: 5, r: true|)))
