#include <iostream>
#include <unordered_map>
#include <string>

using namespace std;


int multByTwo(int a){ return a * 2; }

int gimmeZero(int a){ return 0; }

int modByThree(int a){ return a % 3; }

int subtractTen(int a){ return a - 10; }

int main(int argc, char** argv){
	unordered_map<string, int> intMap;

	intMap.emplace("one", 1);
	intMap.emplace("two", 2);
	intMap.emplace("three", 3);
	intMap.emplace("four", 4);
	intMap.emplace("five", 5);

	cout << intMap.at("one") << ',' << intMap.at("two") << ',' << intMap.at("three") << ',' << intMap.at("four") << ',' << intMap.at("five") << endl;

	//string j = "";
	//while (j != "quit"){
	//	cin >> j;
	//	try {

	//		cout << intMap.at(j) << endl;
	//	}
	//	catch (exception){}
	//}

	unordered_map<string, int(*)(int)> funcMap;
	funcMap.emplace("multByTwo", &multByTwo);
	funcMap.emplace("gimmeZero", &gimmeZero);
	funcMap.emplace("modByThree", &modByThree);
	funcMap.emplace("subtractTen", &subtractTen);

	int k;
	while (1){
		cin >> k;
		cout << endl;
		cout << "Multiplied by 2: " << funcMap.at("multByTwo")(k) << endl;
		cout << "Zeroed out: " << funcMap.at("gimmeZero")(k) << endl;
		cout << "Modded by 3: " << funcMap.at("modByThree")(k) << endl;
		cout << "Subtract 10: " << funcMap.at("subtractTen")(k) << endl;
	}

	return 0;
}

