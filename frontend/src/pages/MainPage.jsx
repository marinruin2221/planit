import React from "react";

import Header from "@components/common/Header.jsx";
import Footer from "@components/common/Footer.jsx";

import SearchForm from "@components/main/SearchForm.jsx";

export default function MainPage()
{
	return <React.Fragment>
		<Header/>
		
		<SearchForm/>

		<Footer/>
	</React.Fragment>
}