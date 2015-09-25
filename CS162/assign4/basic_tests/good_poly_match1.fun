type Foo ['A] = Bar 'A

case Foo!Bar<num>(3) of
| Bar(b) => b
