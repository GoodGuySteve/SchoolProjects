type EitherNB = Left bool
              | Right num

let eitherL = EitherNB!Left(true),
    eitherR = EitherNB!Right(1),
    eitherM = (rec mapInner: ((num) => num, EitherNB) => EitherNB =
                (f: (num) => num, xs: EitherNB) =>
                    case xs of
                      | Left(b) => EitherNB!Left(b)
                      | Right(n) => EitherNB!Right(f(n))
                in mapInner)
  in eitherM((n: num) => n + 1, eitherL)


