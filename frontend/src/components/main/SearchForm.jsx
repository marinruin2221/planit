import React from "react";

export default function MainSearchForm()
{
	return <React.Fragment>
		<form className="flex flex-row gap-2">
			<div className="flex flex-1 flex-col gap-2 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500" htmlFor="tour_word">여행지, 숙소 검색</label>
				<input className="text-xl text-gray-500" id="tour_word"/>
			</div>
			<div className="flex flex-1 flex-col gap-2 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500" htmlFor="tour_from">가는 날</label>
				<input className="text-xl text-gray-500" id="tour_from"/>
			</div>
			<div className="flex flex-1 flex-col gap-2 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500" htmlFor="tour_to">오는 날</label>
				<input className="text-xl text-gray-500" id="tour_to"/>
			</div>
			<div className="flex flex-1 flex-col gap-2 p-2 rounded-sm bg-gray-100">
				<label className="text-xs text-gray-500" htmlFor="tour_size">인원</label>
				<input className="text-xl text-gray-500" id="tour_size"/>
			</div>
			<button	className="flex-1 p-2 rounded-sm text-xl text-white bg-blue-500">검색하기</button>
		</form>
	</React.Fragment>
}