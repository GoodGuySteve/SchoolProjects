type EitherNB = Left bool
              | Right num

case EitherNB!Left(true) of
| Left(b) => EitherNB!Left(b)

