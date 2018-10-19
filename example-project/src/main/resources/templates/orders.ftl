<html>
<head>
    <title>Joker DEMO</title>
</head>
<body>


<style type="text/css">
    .tg  {border-collapse:collapse;border-spacing:0;border-color:#999;}
    .tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#999;color:#444;background-color:#F7FDFA;}
    .tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#999;color:#fff;background-color:#26ADE4;}
    .tg .tg-84g4{font-size:20px;text-align:left;vertical-align:top}

    .new-select-style select {
        border-radius: 0;
        background: #F7FDFA;
        height: 34px;
        padding: 5px;
        border: 2;
        border-color: #26ADE4;
        font-size: 16px;
        line-height: 1;
        -webkit-appearance: none;
        width: 500px;
    }

    .button {
        background-color: #26ade4;
        border: none;
        color: white;
        padding: 15px 25px;
        text-align: center;
        font-size: 16px;
        cursor: pointer;
    }

    .button:hover {
        background-color: #186f93;
    }

</style>

<h1>Пользователь</h1>
<div class="new-select-style">
    <select onchange="window.document.location.href='/orders?USER_ID=' + this.options[this.selectedIndex].value;">
        <option value="">How are you?</option>
        <#list users as user>
            <option
                    <#if currentUserId ??>
                        <#if user.id == currentUserId> selected</#if>
                    </#if>
                    value="${user.id}">${user.firstName} ${user.lastName} (${user.role} в ${user.branchId}, лимит утверждений ${user.maxOrderAmount})</option>
        </#list>
    </select>
</div>
<br />
<br />

<h1>Заказы</h1>
<table class="tg">
    <thead>
        <th class="tg-84g4">ИД</th>
        <th class="tg-84g4">Объем</th>
        <th class="tg-84g4">ИД Филиала</th>
        <th class="tg-84g4">Клиент</th>
        <th class="tg-84g4">Статус</th>
        <th class="tg-84g4">Разрешенные действия</th>
    </thead>
    <tbody>
        <#list orders as order>
            <tr>
                <td>${order.id}</td>
                <td>${order.amount}</td>
                <td>${order.branchId}</td>
                <td>${order.customer.firstName} ${order.customer.firstName}</td>
                <td>${order.state}</td>
            </tr>
        </#list>
    </tbody>
</table>

</body>
</html>