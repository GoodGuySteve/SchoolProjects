type Foo ['A] = Bar 'A | Baz 'A

case Foo!Bar<num>(3) of
| Bar(b) => b
| Baz(b) => b
