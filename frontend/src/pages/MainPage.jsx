import React from "react";
import { useEffect } from "react";

import Header from "@layout/Header.jsx";
import Footer from "@layout/Footer.jsx";

export default function MainPage()
{
	useEffect(() => {
		fetch("/api/message")
		.then((res) => res.text())
		.then((data) => console.log(data))
	}, []);

	return <React.Fragment>
		<Header/>
		mainpage
		<Footer/>
	</React.Fragment>
}