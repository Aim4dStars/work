<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<body style="padding: 10px">
<style>
table {
    border: 1px solid black;
    border-spacing: 10px;
}
th, td {
    border: 1px solid black;
    margin: 0px;
    padding: 8px;
}
th {
    text-align: left;
    background-color: lightgrey;
}
.toggle tbody tr.on {
    background-color: #DDFFDD;
}
.toggle tbody tr.on:hover {
    background-color: #CCEECC;
}
.toggle tbody tr.off {
    background-color: #FFDDDD;
}
.toggle tbody tr.off:hover {
    background-color: #EECCCC;
}
</style>

<h3>Feature toggles for ${machineName}</h3>
<table class="toggle">
<thead>
    <tr><th>Feature</th><th>Status</th><th>Flip</th></tr>
</thead>
<tbody>
    <c:forEach items="${toggles}" var="toggle">
        <tr class="${toggle.value ? 'on' : 'off'}">
            <td><a name="${toggle.key}">${toggle.key}</a></td>
            <td>${toggle.value}</td>
            <td><form action="toggles/${toggle.key}/set" method="post">
                    <input type="hidden" name="token" value="${token}"/>
                    <input type="hidden" name="cssftoken" value="${cssftoken}"/>
                    <input type="hidden" name="toggle" value="${!toggle.value}"/>
                    <input type="submit" value="Change"/>
                </form>
            </td>
        </tr>
    </c:forEach>
</tbody>
</table>

<form action="toggles/add" method="post">
<table style="margin-top: 20px">
<thead>
    <tr><th colspan="2">Add new toggle</th></tr>
</thead>
<tbody>
    <tr>
        <td>
            <input type="hidden" name="token" value="${token}"/>
            <input type="hidden" name="cssftoken" value="${cssftoken}"/>
            <label for="name">Name</label>
            <input type="text" name="name" size="20" maxlength="100" required/>
        </td>
        <td>
            <label for="toggle">Status</label>
            <input type="checkbox" value="true" name="toggle">
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Add toggle" />
        </td>
    </tr>
</tbody>
</table>
</form>
<hr>
<a href="home">&lt;&lt; Home</a>
</body>