const authorizedContent = document.getElementById('authorizedContent');
const unauthorizedContent = document.getElementById('unauthorizedContent');
const optionsForPeriodDiv = document.getElementById('optionsForPeriodDiv');
const optionsForSupplyDiv = document.getElementById('optionsForSupplyDiv');

let productTypes = [];
let productCountForPeriod = 0;
let productCountForSupply = 0;

let username;

const reportTable = document.getElementById('reportTable');
const reportDiv = document.getElementById('2 fields');

function getReportFromServer(){
    const fromDateValue = document.getElementById('fromDateForReport').value;
    const toDateValue = document.getElementById('toDateForReport').value;

    if (!fromDateValue || !toDateValue){
        alert('Выберите дату');
        return;
    }

    fetch(`http://localhost:8080/supply-app/createReport?fromDate=${fromDateValue}&toDate=${toDateValue}`, {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok){
            alert('Данных за этот период нет!');
            return null;
        }
        return response.json()
    })
    .then(data => {
        if (!data) return;
        displayReport(data)
    });
}

function displayReport(data){
    reportTable.innerHTML = '';
    reportDiv.innerHTML = '';
    
    reportTable.innerHTML = `
        <tr style="text-align: left;">
        <th width="200" style="text-align: center">Поставщик</th>
        <th width="350" style="text-align: center">Продукт</th>
        <th width="350" style="text-align: center">Общий вес (кг)</th>
        <th width="350" style="text-align: center">Общая стоимость (руб)</th>
        </tr>
    `;

    data.rows.forEach(row => {
        reportTable.insertAdjacentHTML(
            'beforeend',
            `
            <tr style="border: 1px solid black;">
                <td style="text-align: left;">${row.supplier}</td>
                <td style="text-align: left;">${row.product}</td>
                <td style="text-align: left;">${row.total_weight}</td>
                <td style="text-align: left;">${row.total_cost}</td>
            </tr>
            `
        );
    });

    reportDiv.innerHTML = `
    <p>Общий вес продуктов: ${data.weightOfAllProducts} кг</p>
    <p>Общая стоимость продуктов: ${data.costOfAllProducts} руб</p>
    `;
}

function addProductForSupply(){
    const select = createProductSelect('supply');
    const input = createWeightInputForSupply();

    const div = document.createElement('div');
    div.style="margin-bottom: 3px;";
    div.appendChild(select);
    div.appendChild(input);

    optionsForSupplyDiv.appendChild(div);
}

function createWeightInputForSupply(){
    const input = document.createElement('input');
    input.id = 'weightInputForSupply' + productCountForSupply;
    input.type = 'number';
    input.min = 0;
    input.placeholder = 'Вес (кг)';
    return input;
}

function addProductForPeriod(){
    const select = createProductSelect('period');
    const input = createPriceInput();

    const div = document.createElement('div');
    div.style = "margin-bottom: 3px;";
    div.appendChild(select);
    div.appendChild(input);

    optionsForPeriodDiv.appendChild(div);
}

function createProductSelect(type){
    let selectId = 
        type === 'period' ?
        'selectForPeriod' + ++productCountForPeriod :
        'selectForSupply' + ++productCountForSupply;

    const select = document.createElement('select');
    select.id = selectId;
    select.style="margin-right: 10px;";

    productTypes.forEach(product => {
        const option = document.createElement('option');
        option.value = product;
        option.textContent = product;
        select.appendChild(option);
    });

    return select;
}

function createPriceInput(){
    const input = document.createElement('input');
    input.id = 'priceInputForPeriod' + productCountForPeriod;
    input.placeholder = 'Цена за кг';
    input.type= 'number';
    input.min = 0;

    return input;
}

function sendSupplyOnServer(){
    const data = [];
    const map = new Map();

    for (let i = productCountForSupply; i > 0; i--){
        const selectValue = document.getElementById(`selectForSupply${i}`).value;
        const inputValue = document.getElementById(`weightInputForSupply${i}`).value;

        data.push({
            username: username,
            productType: selectValue,
            weight: parseFloat(inputValue),
        });
    }

    fetch('http://localhost:8080/supply-app/saveSupply', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
    .then(response => response.text())
    .then(data => alert(data));
}

function sendPeriodOnServer(){
    const data = [];

    const fromDateValue = document.getElementById('fromDateForPeriod').value;
    const toDateValue = document.getElementById('toDateForPeriod').value;

    for (let i = productCountForPeriod; i > 0; i--){
        const selectValue = document.getElementById(`selectForPeriod${i}`).value;
        const inputValue = document.getElementById(`priceInputForPeriod${i}`).value;

        data.push({
            fromDate: fromDateValue,
            toDate: toDateValue,
            productType: selectValue,
            price: inputValue,
            username: username
        });
    }

    fetch('http://localhost:8080/supply-app/savePrices',{
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data) 
    })
    .then(response => response.text())
    .then(data => alert(data));
}

function showAuthorizedContent(){
    fetch('http://localhost:8080/supply-app/getProductTypes', {
        method: 'GET'
    })
    .then(response => response.json())
    .then(data => productTypes = data);

    document.getElementById('authorizedContent').style.display = 'block';
    document.getElementById('unauthorizedContent').style.display = 'none';
}

function showUnauthorizedContent(){
    document.getElementById('authorizedContent').style.display = 'none';
    document.getElementById('unauthorizedContent').style.display = 'block';
}

function signIn(){
    const usernameValue = document.getElementById('usernameForSignIn').value;
    const passwordInput = document.getElementById('passwordForSignIn');

    const data = new URLSearchParams();
    data.append('username', usernameValue);
    data.append('password', passwordInput.value);

    fetch('http://localhost:8080/supply-app/signIn', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: data
    })
    .then(response => response.json())
    .then(data => {
        if (data.length === 2){
            username = data[0];
            showAuthorizedContent();
            alert(data[1]);
        } else {
            passwordInput.value = '';
            alert(data[0]);
        }
        console.log(username);
    });
}

function signUp(){
    const usernameInput = document.getElementById('usernameForSignUp');
    const passwordInput = document.getElementById('passwordForSignUp');
    const usernameValue = usernameInput.value;
    const passwordValue = passwordInput.value;

    fetch('http://localhost:8080/supply-app/signUp', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username: usernameValue, password: passwordValue})    
    })
    .then(response => response.json())
    .then(data => {
        if (data.length === 2){
            username = data[0];
            showAuthorizedContent();
            alert(data[1]);
        } else {
            usernameInput.value = '';
            passwordInput.value = '';
            alert(data[0]);
        }
        console.log(username);
    });
}

