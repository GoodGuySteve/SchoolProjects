type EitherNB = Left bool
              | Right num

case EitherNB!Left(true) of
| Left(b) => b
| Right(b) => b
