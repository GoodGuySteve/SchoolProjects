let f = (['A, 'B](a: 'A, g: ('A) => 'B) => g(a)) in f<num, bool>(1, (x: num) => x < 5)
