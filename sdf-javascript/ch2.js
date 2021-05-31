const compose = (f, g) => {
  return function(...args) {
    return f(g.apply(null, args))
  }
}

const list = (...args) => args;

/*
let result = compose((x) => list("foo", x),
  (x) => list("bar", x))("z");
 */

const identity = (x) => x;

const iterate = (n, f) => {
  return (n == 0) ? identity : compose(f, iterate(n-1, f));
}

const square = (x) => x*x;

// iterate(3, square)(5);