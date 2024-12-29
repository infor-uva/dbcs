export function getBasePath(route: string) {
  const url = route.split('/').slice(1);
  const me = 'me';
  let base = me;
  if (url.length > 0 && url[0] === me) {
    base = url[0];
  } else if (url.length > 0 && url[0] === 'admin') {
    const i = url.indexOf('users');
    const j = url.indexOf('hotels');
    base =
      i !== -1
        ? url.slice(0, i + 2).join('/')
        : j !== -1
        ? url.slice(0, i + 2).join('/')
        : me;
  }

  return `/${base}`;
}
