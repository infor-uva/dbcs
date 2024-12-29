const { jwtDecode } = require("jwt-decode");
const axios = require("axios");
const args = process.argv;

const main = async (params) => {
	console.log("Peitción");

	const response = await axios.post("http://localhost:8101/token/service", {
		service: "User",
	});

	const r = jwtDecode(response.data.token);
	// console.log(JSON.stringify(r, null, 2));
	console.log(JSON.stringify(response.data.data, null, 2));
};

main();
