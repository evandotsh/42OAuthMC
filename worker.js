
export default {
  async fetch(request, env) {
    const { pathname } = new URL(request.url)

    const uuid = pathname.split('/')[1]

    if (pathname === `/success`) {
      // check if request is to base_url/link/success
      const code = new URL(request.url).searchParams.get('code')
      const state = new URL(request.url).searchParams.get('state')

      // exchange oauth code for access token
      const tokenResponse = await fetch(`${env.INTRA_BASE_URL}/oauth/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'authorization_code',
          code,
          redirect_uri: env.INTRA_REDIRECT_URI,
          client_id: env.INTRA_CLIENT_ID,
          client_secret: env.INTRA_CLIENT_SECRET
        }),
      })

      const tokenData = await tokenResponse.json()

      if (!tokenData.access_token) {
        // if access token is not returned, return 401
        return new Response(null, { status: 401 })
      }

      // get user's username from intra API using access token
      const userDataResponse = await fetch(`${env.INTRA_BASE_URL}/v2/me`, {
        headers: { Authorization: `Bearer ${tokenData.access_token}` },
      })
      const userData = await userDataResponse.json()

      if (!userData.login) {
        // if username is not returned, return 401
        return new Response(null, { status: 401 })
      }

      // store uuid and username in worker KV
      if (await env.ACCOUNTS.get(state) == userData.login)
        return new Response('This minecraft account is already linked to your intra account')
      await env.ACCOUNTS.put(state, userData.login)
      return new Response('Successfully linked account')
    }

    if (pathname === `/${uuid}`) {
      // check if request is to base_url/uuid
      const value = await env.ACCOUNTS.get(uuid)

      if (value) {
        // if value exists, return 200 with body
        return new Response(JSON.stringify({ intra_account: value }), {
          headers: { 'Content-Type': 'application/json' },
        })
      } else {
        // if value does not exist, return 401
        return new Response(null, { status: 401 })
      }
    }

    if (pathname === `/${uuid}/link`) {
      // check if request is to base_url/uuid/link
      // redirect user to 42 intra oauth page
      const redirectUrl = `${env.INTRA_BASE_URL}/oauth/authorize?client_id=${env.INTRA_CLIENT_ID}&response_type=code&redirect_uri=${encodeURIComponent(env.INTRA_REDIRECT_URI)}&state=${uuid}`
      return Response.redirect(redirectUrl, 302)
    }

    // if request does not match any of the above, return 404
    return new Response(null, { status: 404 })
  }
}
